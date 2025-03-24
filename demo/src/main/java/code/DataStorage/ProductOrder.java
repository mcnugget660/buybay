package code.DataStorage;

import java.time.LocalDateTime;

//A productInstance is a record of a purchase, amount and other relevant data needed for a purchase
public class ProductOrder {

	private int amount;

	private LocalDateTime purchaseDate;

	private String buyerUsername;

	private Product product; // In case seller removes the product

	private ProductOrder(Product product, int amount, String buyerUsername) {
		this.product = product;
		this.amount = amount;
		this.buyerUsername = buyerUsername;
		purchaseDate = LocalDateTime.now();
	}

	public static ProductOrder placeOrder(int id, int amount, Account buyer) {
		Product product = ProductManager.PRODUCTMANAGER.fromID(id);

		Account seller = AccountManager.ACCOUNTMANAGER.getAccount(product.getSeller());
		if (seller == null || buyer == null) {
			System.out.println("Failed to place order due to internal error");
			return null;
		}

		if (product.removeStock(amount)) {
			// Transfer money to the seller
			// Remove money from the buyer
			// Money can be transfered to BuyBay and only that money can be used to purchase
			// items
			return new ProductOrder(product, amount, buyer.getUsername());
		}

		return null;
	}

	public void complete() {
		Account buyer = AccountManager.ACCOUNTMANAGER.getAccount(buyerUsername);
		Account seller = AccountManager.ACCOUNTMANAGER.getAccount(product.getSeller());
		if (buyer == null || seller == null) {
			System.out.println("Order completion failed - will have to be fixed manually");
		}
		// Order was received by the buyer; do something
	}

}
