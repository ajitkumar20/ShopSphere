import { createBrowserRouter } from "react-router-dom";
import App from "./App.jsx";
import ProductListPage from "./pages/ProductListPage/ProductListPage.jsx";
import ShopApplicationWrapper from "./pages/ShopApplicationWrapper.jsx";
import ProductDetails from "./pages/ProductDetailsPage/ProductDetails.jsx";
import { loadProductBySlug } from "./routes/products.js";

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
]);
