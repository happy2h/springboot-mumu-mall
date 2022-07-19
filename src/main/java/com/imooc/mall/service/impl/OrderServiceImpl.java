package com.imooc.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imooc.mall.common.Constant;
import com.imooc.mall.exception.ImoocMailExceptionEnum;
import com.imooc.mall.exception.ImoocMallException;
import com.imooc.mall.filter.UserFilter;
import com.imooc.mall.model.dao.CartMapper;
import com.imooc.mall.model.dao.OrderItemMapper;
import com.imooc.mall.model.dao.OrderMapper;
import com.imooc.mall.model.dao.ProductMapper;
import com.imooc.mall.model.pojo.Order;
import com.imooc.mall.model.pojo.OrderItem;
import com.imooc.mall.model.pojo.Product;
import com.imooc.mall.model.request.CreateOrderReq;
import com.imooc.mall.model.vo.CartVO;
import com.imooc.mall.model.vo.OrderItemVO;
import com.imooc.mall.model.vo.OrderVO;
import com.imooc.mall.service.CartService;
import com.imooc.mall.service.OrderService;
import com.imooc.mall.service.UserService;
import com.imooc.mall.util.OrderCodeFactory;
import com.imooc.mall.util.QRCodeGenerator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("orderService")
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private CartService cartService;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private UserService userService;

    @Value("${file.upload.ip}")
    String ip;


    // 创建订单
    // 添加数据库事务
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public String create(CreateOrderReq createOrderReq){
        // 拿到用户id
        Integer userId = UserFilter.currentUser.getId();
        // 从购物车查找已经勾选的商品
        List<CartVO> cartVOList = cartService.list(userId);
        List<CartVO> cartVOListTemp = new ArrayList<>();
        for(CartVO cartVO : cartVOList){
            if(cartVO.getSelected().equals(Constant.Cart.CHECKED)){
                cartVOListTemp.add(cartVO);
            }
        }
        cartVOList = cartVOListTemp;
        // 如果购物车已勾选的为空 则报错
        if(CollectionUtils.isEmpty(cartVOList)){
            throw new ImoocMallException(ImoocMailExceptionEnum.CART_EMPTY);
        }
        // 判断商品是否存在。上下架状态， 库存
        validSaleStatusAndStock(cartVOList);
        // 把购物车对象转换为订单item对象
        List<OrderItem> orderItemList = cartVOListToOrderItemList(cartVOList);
        // 扣库存
        for (OrderItem orderItem: orderItemList) {
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            int stock = product.getStock() - orderItem.getQuantity();
            if(stock < 0){
                throw new ImoocMallException(ImoocMailExceptionEnum.NOT_ENOUGH);
            }
            product.setStock(stock);
            // 更新
            productMapper.updateByPrimaryKeySelective(product);
        }
        // 把购物车中已勾选的商品删除
        cleanCart(cartVOList);
        // 生成订单号
        Order order = new Order();
        String orderNo = OrderCodeFactory.getOrderCode(Long.valueOf(userId));
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalPrice(totalPrice(orderItemList));
        order.setReceiverName(createOrderReq.getReceiverName());
        order.setReceiverAddress(createOrderReq.getReceiverAddress());
        order.setReceiverMobile(createOrderReq.getReceiverMobile());
        order.setOrderStatus(Constant.OrderStatusEnum.NOT_PAY.getCode());
        order.setPostage(0);
        order.setPaymentType(1);
        // 插入到order表中
        orderMapper.insertSelective(order);
        // 循环保存每个商品到order_item表, 返回结果
        for(OrderItem orderItem : orderItemList){
            orderItem.setOrderNo(orderNo);
            orderItemMapper.insertSelective(orderItem);
        }
        return orderNo;
    }

    // 订单详情页 涉及两个表
    @Override
    public OrderVO detail(String orderNo){
        Order order = orderMapper.selectByOrderNo(orderNo);
        if(order == null){
            throw new ImoocMallException(ImoocMailExceptionEnum.NO_ORDER);
        }
        // 订单存在, 判断用户是否一致
        if(order.getUserId() != UserFilter.currentUser.getId()){
            throw new ImoocMallException(ImoocMailExceptionEnum.NOT_YOUR_ORDER);
        }
        return getOrderVo(order);
    }

    /**
     * 用户列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageInfo listForCustomer(Integer pageNum, Integer pageSize){
        Integer userId = UserFilter.currentUser.getId();
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectForCustomer(userId);
        List<OrderVO> orderVOList = orderListToOrderVOList(orderList);
        // 包装
       return new PageInfo<>(orderVOList);
    }

    /**
     * 管理员列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageInfo listForAdmin(Integer pageNum, Integer pageSize){
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectAllForAdmin();
        List<OrderVO> orderVOList = orderListToOrderVOList(orderList);
        return new PageInfo<>(orderVOList);
    }

    /**
     * 取消订单,单表操作
     * @param orderNo
     */
    @Override
    public void cancel(String orderNo){
        Order order = orderMapper.selectByOrderNo(orderNo);
        // 查不到订单报错
        if(order == null){
            throw new ImoocMallException(ImoocMailExceptionEnum.NO_ORDER);
        }
        // 验证用户身份
        if(order.getUserId() != UserFilter.currentUser.getId()){
            throw new ImoocMallException(ImoocMailExceptionEnum.NOT_YOUR_ORDER);
        }
        // 判断订单支付状态
        if(order.getOrderStatus().equals(Constant.OrderStatusEnum.NOT_PAY.getCode())){
            order.setOrderStatus(Constant.OrderStatusEnum.CANCELED.getCode());
            // 订单取消 表示完结
            order.setEndTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        } else {
            throw new ImoocMallException(ImoocMailExceptionEnum.WRONG_ORDER_STATUS);
        }
    }

    /**
     * 生成支付二维码
     * @param orderNo
     * @return
     */
    @Override
    public String qrcode(String orderNo){
        // 获得端口号
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String addr = ip + ":" + request.getLocalPort();
        String payUrl = "http://" + addr + "/pay?orderNo=" + orderNo;
        try {
            QRCodeGenerator.generatorQRCodeImage(payUrl, 350, 350, Constant.FILE_UPLOAD_DIR + orderNo + ".png");
        } catch (Exception e) {
            e.printStackTrace();
            throw new ImoocMallException(ImoocMailExceptionEnum.GENERATE_QRCODE_FAILED);
        }
        // ImoocMallMvcConfig 对应的过滤器一致
        String pngAddr = "http://" + addr + "/static/images/" + orderNo + ".png";
        return pngAddr;
    }

    /**
     * 订单支付
     * @param orderNo
     */
    @Override
    public void pay(String orderNo){
        Order order = orderMapper.selectByOrderNo(orderNo);
        // 查不到订单报错
        if(order == null){
            throw new ImoocMallException(ImoocMailExceptionEnum.NO_ORDER);
        }
        if(order.getOrderStatus().equals(Constant.OrderStatusEnum.NOT_PAY.getCode())){
            // 设置为已支付的状态
            order.setOrderStatus(Constant.OrderStatusEnum.PAID.getCode());
            order.setPayTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        } else {
            throw new ImoocMallException(ImoocMailExceptionEnum.WRONG_ORDER_STATUS);
        }
    }

    /**
     * 发货 - 管理员
     * @param orderNo
     */
    @Override
    public void deliver(String orderNo){
        Order order = orderMapper.selectByOrderNo(orderNo);
        // 查不到订单报错
        if(order == null){
            throw new ImoocMallException(ImoocMailExceptionEnum.NO_ORDER);
        }
        if(order.getOrderStatus().equals(Constant.OrderStatusEnum.PAID.getCode())){
            order.setOrderStatus(Constant.OrderStatusEnum.DELIVERED.getCode());
            order.setDeliveryTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        } else {
            throw new ImoocMallException(ImoocMailExceptionEnum.WRONG_ORDER_STATUS);
        }
    }

    /**
     * 订单完结
     * @param orderNo
     */
    @Override
    public void finish(String orderNo){
        Order order = orderMapper.selectByOrderNo(orderNo);
        // 查不到订单报错
        if(order == null){
            throw new ImoocMallException(ImoocMailExceptionEnum.NO_ORDER);
        }
        // 如果是普通用户需要校验订单所属
        // 不是管理员 并且不是自己的订单
        if (!userService.checkAdminRole(UserFilter.currentUser) && !order.getUserId().equals(UserFilter.currentUser.getId())){
            throw new ImoocMallException(ImoocMailExceptionEnum.NOT_YOUR_ORDER);
        }
        // 执行到这里 要么是管理员 要么是用户操作自己的订单 发货可以完结订单
        if(order.getOrderStatus().equals(Constant.OrderStatusEnum.DELIVERED.getCode())){
            order.setOrderStatus(Constant.OrderStatusEnum.FINISHED.getCode());
            order.setEndTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        } else {
            throw new ImoocMallException(ImoocMailExceptionEnum.WRONG_ORDER_STATUS);
        }
    }

    private List<OrderVO> orderListToOrderVOList(List<Order> orderList) {
        List<OrderVO> orderVOList = new ArrayList<>();
        for(Order order : orderList){
            orderVOList.add(getOrderVo(order));
        }
        return orderVOList;
    }

    private OrderVO getOrderVo(Order order) {
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order, orderVO);
        // 获取订单对应的orderItemList
        List<OrderItem> orderItems = orderItemMapper.selectByOrderNo(order.getOrderNo());
        List<OrderItemVO> orderItemVOList = new ArrayList<>();
        for(OrderItem orderItem : orderItems){
            OrderItemVO orderItemVO = new OrderItemVO();
            BeanUtils.copyProperties(orderItem, orderItemVO);
            orderItemVOList.add(orderItemVO);
        }
        orderVO.setOrderItemVOList(orderItemVOList);
        orderVO.setOrderStatusName(Constant.OrderStatusEnum.codeOf(order.getOrderStatus()).getValue());
        return orderVO;
    }

    private Integer totalPrice(List<OrderItem> orderItemList) {
        int totalPrice = 0;
        for(OrderItem orderItem : orderItemList){
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }

    private void cleanCart(List<CartVO> cartVOList) {
        for(CartVO cartVO : cartVOList){
            cartMapper.deleteByPrimaryKey(cartVO.getId());
        }
    }

    private List<OrderItem> cartVOListToOrderItemList(List<CartVO> cartVOList) {
        List<OrderItem> orderItemList = new ArrayList<>();
        for(CartVO cartVO : cartVOList){
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(cartVO.getProductId());
            orderItem.setQuantity(cartVO.getQuantity());
            // 记录商品快照信息
            orderItem.setProductName(cartVO.getProductName());
            orderItem.setProductImg(cartVO.getProductImage());
            orderItem.setUnitPrice(cartVO.getPrice());
            orderItem.setTotalPrice(cartVO.getTotalPrice());
            orderItemList.add(orderItem);
        }
        return orderItemList;
    }

    private void validSaleStatusAndStock(List<CartVO> cartVOList) {
        for (CartVO cartVO: cartVOList){
            Product product = productMapper.selectByPrimaryKey(cartVO.getProductId());
            // 商品是否上架
            if(product == null || product.getStatus().equals(Constant.SaleStatus.NOT_SALE)){
                throw new ImoocMallException(ImoocMailExceptionEnum.NOT_SALE);
            }
            // 判断商品库存
            if(product.getStock() < cartVO.getQuantity()){
                throw new ImoocMallException(ImoocMailExceptionEnum.NOT_ENOUGH);
            }
        }
    }
}
