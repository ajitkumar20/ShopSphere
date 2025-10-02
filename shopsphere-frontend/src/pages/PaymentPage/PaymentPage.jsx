import { Elements } from '@stripe/react-stripe-js';
import React from 'react';
import { loadStripe } from '@stripe/stripe-js';
import CheckoutForm from './CheckoutForm';


const stripePromise = loadStripe(import.meta.env.VITE_STRIPE_PUBLISHABLE_KEY);

const PaymentPage = (props) => {

  const options = {
    // mode: 'payment',
    // amount: 1,
    // currency: 'usd',
    // Fully customizable with appearance API.
    appearance: {
      theme: 'flat'
    },
    clientSecret: import.meta.env.VITE_STRIPE_CLIENT_SECRET,
  };

  return (
    <div>
      <Elements stripe={stripePromise} options={options}>
        <CheckoutForm {...props}/>
      </Elements>
    </div>
  )
}

export default PaymentPage;