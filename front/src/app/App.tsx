import {BrowserRouter, Route, Routes} from "react-router-dom";
import {Provider} from "react-redux";
import {store} from "@/store/store";
import {Header} from "@/shared/components/header/ui/Header";
import Footer from "@/shared/components/footer/Footer";
import RegistrationPage from "@/pages/registeration/ui/RegistrationPage.tsx";
import LoginPage from "@/pages/login/ui/LoginPage.tsx";

function App() {
    return (
        <BrowserRouter>
            <Provider store={store}>
                <Header/>

                <Routes>
                    <Route path="/registration" element={<RegistrationPage/>}/>
                    <Route path="/login" element={<LoginPage/>}/>
                </Routes>

                <Footer/>
            </Provider>
        </BrowserRouter>
    );
}
//поправить название токенов
export default App;
