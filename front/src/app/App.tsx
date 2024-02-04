import {BrowserRouter, Route, Routes} from "react-router-dom";
import {Provider} from "react-redux";
import {store} from "@/store/store";
import {Header} from "@/shared/components/header/ui/Header";
import Footer from "@/shared/components/footer/Footer";

function App() {
    return (
        <BrowserRouter>
            <Provider store={store}>
                <Header/>

                <Routes>
                    {/*<Route path="" element={<ProductPage/>}/>*/}
                </Routes>
                <Footer/>
            </Provider>
        </BrowserRouter>
    );
}

export default App;
