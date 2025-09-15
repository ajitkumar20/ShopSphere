import { createBrowserRouter } from "react-router-dom";
import App from "./App.jsx";
import ProductListPage from "./pages/ProductListPage/ProductListPage.jsx";
import ShopApplicationWrapper from "./pages/ShopApplicationWrapper.jsx";

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
        element: <ProductListPage categoryType={"WOMEN"}/>,
      },
      {
        path: "/men",
        element: <ProductListPage categoryType={"MEN"}/>,
      },
    ],
  },
]);
