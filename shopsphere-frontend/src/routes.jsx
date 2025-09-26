import { createBrowserRouter } from "react-router-dom";
import App from "./App.jsx";
import ProductListPage from "./pages/ProductListPage/ProductListPage.jsx";
import ShopApplicationWrapper from "./pages/ShopApplicationWrapper.jsx";
import ProductDetails from "./pages/ProductDetailsPage/ProductDetails.jsx";
import { loadProductBySlug } from "./routes/products.js";
import AuthenticationWrapper from "./pages/AuthenticationWrapper.jsx";
import Login from "./pages/Login/Login.jsx";
import Register from "./pages/Register/Register.jsx";
import OAuth2LoginCallback from "./pages/OAuth2LoginCallback.jsx";

export const router = createBrowserRouter([
  {
    path: "/",
    element: <ShopApplicationWrapper />,
    children: [
      {
        path: "/",
        element: <App />,
      },
      {
        path: "/women",
        element: <ProductListPage categoryType={"WOMEN"} />,
      },
      {
        path: "/men",
        element: <ProductListPage categoryType={"MEN"} />,
      },
      {
        path: "/product/:slug",
        loader: loadProductBySlug,
        element: <ProductDetails />,
      },
    ],
  },
  {
    path: "/v1/",
    element: <AuthenticationWrapper />,
    children: [
      {
        path: "login",
        element: <Login />,
      },
      {
        path: "register",
        element: <Register />,
      },
    ],
  },
  {
    path: "/oauth2/callback",
    element: <OAuth2LoginCallback />,
  },
]);
