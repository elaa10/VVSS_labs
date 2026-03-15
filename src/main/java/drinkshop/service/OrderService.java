package drinkshop.service;

import drinkshop.domain.Order;
import drinkshop.domain.OrderItem;
import drinkshop.domain.Product;
import drinkshop.repository.Repository;

import java.util.List;

public class OrderService {

    private final Repository<Integer, Order> orderRepo;
    private final Repository<Integer, Product> productRepo;

    public OrderService(Repository<Integer, Order> orderRepo, Repository<Integer, Product> productRepo) {
        this.orderRepo = orderRepo;
        this.productRepo = productRepo;
    }

    public void addOrder(Order o) {
        orderRepo.save(o);
    }

    public void updateOrder(Order o) {
        orderRepo.update(o);
    }

    public void deleteOrder(int id) {
        orderRepo.delete(id);
    }

    public List<Order> getAllOrders() {
        return orderRepo.findAll();
    }

    public Order findById(int id) {
        return orderRepo.findOne(id);
    }

    public double computeTotal(Order o) {
        // The Order object now knows how to calculate its correct price by itself,
        // thus eliminating the inefficient dependency on productRepo here.
        return o.getTotalPrice();
    }

    public void addItem(Order o, OrderItem item) {
        // We use the behavior exposed by the entity, respecting encapsulation
        o.addItem(item);
        orderRepo.update(o);
    }

    public void removeItem(Order o, OrderItem item) {
        // We use the behavior exposed by the entity
        o.removeItem(item);
        orderRepo.update(o);
    }
}