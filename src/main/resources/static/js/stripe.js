const stripe = Stripe('pk_test_51N7CdjFDQyVJGiMWcK4jAn34Ec3OeGA7mB5guX7HptNza4246ldq8Q78PQZvQXKGDv4u8vs1Y1noRodDDivDq2BO00phehY15m');
const paymentButton = document.getElementById('paymentButton');

paymentButton.addEventListener('click', () => {
  stripe.redirectToCheckout({
    sessionId: sessionId
  })
});