import {BrowserRouter, Route, Routes} from "react-router-dom";
import {Provider} from "react-redux";
import {store} from "@/store/store";
import {Header} from "@/shared/components/header/ui/Header";
import Footer from "@/shared/components/footer/Footer";
import RegistrationPage from "@/pages/registeration/ui/RegistrationPage.tsx";
import LoginPage from "@/pages/login/ui/LoginPage.tsx";
import ProfilePage from "@/pages/profile/ui/ProfilePage.tsx";
import PrivateRoute from "@/app/PrivateRouter.tsx";

function App() {
    return (
        <BrowserRouter>
            <Provider store={store}>
                <Header/>

                <Routes>
                    <Route path="/registration" element={<RegistrationPage/>}/>
                    <Route path="/login" element={<LoginPage/>}/>
                    <Route
                        path="/profile"
                        element={
                            <PrivateRoute>
                                {" "}
                                <ProfilePage/>{" "}
                            </PrivateRoute>
                        }
                    />
                </Routes>

                <Footer/>
            </Provider>
        </BrowserRouter>
    );
}
//поправить название токенов
export default App;
