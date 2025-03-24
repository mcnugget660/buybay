// Get the selected products from the query string
const urlParams = new URLSearchParams(window.location.search);
const selectedProducts = urlParams.get('selectedProducts');

// Parse the selected products into an array
const productsArray = selectedProducts ? selectedProducts.split(',') : [];

// Render the selected products on the page
const cartList = document.getElementById('cart-list');

productsArray.forEach(function(product) {
    const listItem = document.createElement('li');
    const productImage = document.createElement('img');
    productImage.src = 'Items/${product}.jpg`;  // Replace with your own image path
    const productName = document.createElement('span');
    productName.textContent = product;
    const removeButton = document.createElement('button');
    removeButton.textContent = 'Remove';

    removeButton.addEventListener('click', function() {
        listItem.remove();
    });

    listItem.appendChild(productImage);
    listItem.appendChild(productName);
    listItem.appendChild(removeButton);
    cartList.appendChild(listItem);
});
