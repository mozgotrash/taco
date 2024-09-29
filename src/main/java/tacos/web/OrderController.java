package tacos.web;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import tacos.TacoOrder;
import tacos.User;
import tacos.data.OrderRepository;
import tacos.data.UserRepository;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/orders")
@SessionAttributes("tacoOrder")
public class OrderController {

    @Value("${users.taco.size}")
    private Integer userTacoSize;

    private OrderRepository orderRepo;
    private UserRepository userRepository;

    public OrderController(OrderRepository orderRepo,
                           UserRepository userRepository) {
        this.orderRepo = orderRepo;
        this.userRepository = userRepository;
    }

    @GetMapping("/current")
    public String orderForm() {
        return "orderForm";
    }

    @PostMapping
    public String processOrder(@Valid TacoOrder order, Errors errors, SessionStatus sessionStatus,
                               @AuthenticationPrincipal User user) {
        if (errors.hasErrors()) {
            return "orderForm";
        }
        order.setUser(user);

        orderRepo.save(order);
        sessionStatus.setComplete();

        return "redirect:/";
    }

    @GetMapping("/by-user")
    @ResponseBody
    public List<TacoOrder> ordersForUser(
            @AuthenticationPrincipal User user) {
        Pageable pageable = PageRequest.of(0, userTacoSize);
//        model.addAttribute("orders",
//                orderRepo.findByUserOrderByPlacedAtDesc(user, pageable));
//        return "orderList";
        return orderRepo.findByUserOrderByPlacedAtDesc(user, pageable);
    }

}
