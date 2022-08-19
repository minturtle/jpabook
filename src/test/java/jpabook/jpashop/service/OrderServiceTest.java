package jpabook.jpashop.service;

import jpabook.jpashop.dao.ItemRepository;
import jpabook.jpashop.dao.OrderRepository;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Album;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.dto.OrderItemListDto;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private  OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ItemRepository itemRepository;

    private Member member;
    private OrderItemListDto orderItemListDto;
    private Item item1;
    private Item item2;

    @BeforeEach
    void setUp() {
        member = Member.createMember("김민석", "root11", "1111", "경북 구미시", "대학로 61","금오공과 대학교",true);
        item1 = new Book("어린 왕자", 15000, 30, "김민석", "11234");
        item2 = new Album("김민석 정규 앨범 7집", 50000, 10, "김민석", "김민석 데뷔 20주년 기념");
        orderItemListDto = new OrderItemListDto();
    }

    //아이템 정보 수량 => orderItem 객체 생성 => order에 집어넣음
    @Test
    @DisplayName("orderService 객체 생성")
    void t1() throws Exception {
        //given
        //when
        //then
        assertThat(orderService).isNotNull();
    }

    @Test
    @DisplayName("주문 하기")
    void t2() throws Exception {
        //given
        given(itemRepository.findById(item1.getId())).willReturn(item1);
        orderItemListDto.setItems(List.of(new OrderItemListDto.OrderItemDto(item1.getId(), 5))); //5개의 item1 주문
        //when
        Order order = orderService.order(member, orderItemListDto);
        //then
        assertThat(order.getMember()).isEqualTo(member);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.ORDER);
        assertThat(item1.getStockQuantity()).isEqualTo(25);
    }

    @Test
    @DisplayName("주문하기, 상품 재고보다 많은 주문")
    void t3() throws Exception {
        //given
        given(itemRepository.findById(item1.getId())).willReturn(item1);
        orderItemListDto.setItems(List.of(new OrderItemListDto.OrderItemDto(item1.getId(), 500)));
        //when
        ThrowableAssert.ThrowingCallable throwableFunc = ()->{
            orderService.order(member, orderItemListDto);
        };
        //then
        assertThatThrownBy(throwableFunc).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("주문한 수량이 남은 물건의 수량보다 많습니다.");
    }

    @Test
    @DisplayName("주문 취소하기")
    void t4() throws Exception {
        //given
        given(itemRepository.findById(item1.getId())).willReturn(item1);
        orderItemListDto.setItems(List.of(new OrderItemListDto.OrderItemDto(item1.getId(), 5)));
        Order order = orderService.order(member, orderItemListDto);

        given(orderRepository.findById(order.getId())).willReturn(order);
        //when
        orderService.cancel(order.getId());
        //then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCEL);
    }
}