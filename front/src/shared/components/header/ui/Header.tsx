import {HeaderLogo} from "../components/HeaderLogo";
import {LinksList} from "../components/LinksList";
import s from "./Header.module.scss";
import HeaderLinksIcons from "@/shared/components/header/components/HeaderLinksIcons.tsx";
import {selectIsAuthenticated} from "@/modules/auth/slice.ts";
import {useDispatch, useSelector} from "react-redux";
import {Link} from "react-router-dom";
import {logoutUser} from "@/modules/auth/thunk.ts";
import {AppDispatch} from "@/store/store.tsx";


export const Header = () => {
    const isAuthenticated = useSelector(selectIsAuthenticated);
    const dispatch: AppDispatch = useDispatch();

    const handleLogout = () => {
        dispatch(logoutUser());
    };

    return (
        <div className={s.header}>
            <HeaderLogo/>
            <LinksList/>
            <HeaderLinksIcons/>

            {!isAuthenticated && (
                <div className={s.authButtons}>
                    <Link to="/registration" className={s.item}>
                        Зарегистрироваться
                    </Link>
                    <Link to="/login" className={s.item}>
                        Войти
                    </Link>
                </div>
            )}

            {isAuthenticated && (
                <div className={s.userProfile}>
                    <Link to="/profile" className={s.item}>
                        <p className={s.email}>{localStorage.getItem("email")}</p>
                    </Link>
                    <button onClick={handleLogout} className={s.logoutButton}>
                        Выйти
                    </button>
                </div>
            )}

        </div>
    );
};

export default Header;
