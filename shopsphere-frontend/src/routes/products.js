import content from "../data/content.json";

export const loadProductById = ({ params }) => {
    //console.log("params ", params)
    const product = content?.products?.find((product) => product?.id == params?.productId);
    //console.log(product);
    return {product};
}