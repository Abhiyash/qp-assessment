package app.abhiyash.model;

public class OrderResponse {
    private int orderId;
    private int totalAmount;

    public OrderResponse(){

    }

    public OrderResponse(int orderId,  int totalAmount) {
        this.orderId = orderId;
        this.totalAmount = totalAmount;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }
}
