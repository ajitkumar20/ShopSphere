import { useEffect } from "react";
import "./App.css";
import Footer from "./components/Footer/Footer";
import HeroSection from "./components/HeroSection/HeroSection";
import Category from "./components/Sections/Categories/Category";
import NewArrival from "./components/Sections/NewArrival";
import content from "./data/content.json";
import { fetchCategories } from "./api/fetchCategories";
import { useDispatch } from "react-redux";
import { loadCategories } from "./store/features/category";
import { setLoading } from "./store/features/common";

function App() {

  const dispatch = useDispatch();

  useEffect(() => {
    //dispatch(setLoading(true));
    fetchCategories()
      .then((res) => {
        dispatch(loadCategories(res));
      })
      .catch((err) => {
        console.log("Error: ", err);
      }).finally(() => {
        dispatch(setLoading(false));
      })
  }, [dispatch]);

  return (
    <>
      <HeroSection />
      <NewArrival />
      {content?.pages?.shop?.sections &&
        content?.pages?.shop?.sections?.map((item, index) => (
          <Category key={item?.title + index} {...item} />
        ))}
      <Footer content={content?.footer} />
    </>
  );
}

export default App;
