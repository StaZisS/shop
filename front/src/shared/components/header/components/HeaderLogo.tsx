import {Link} from "react-router-dom";
import logo from "@/assets/img/shop-logo.svg";
import s from "./HeaderLogo.module.scss";

export const HeaderLogo = () => (
    <Link to={"/"}>
        <div>
            <img className={s.logo} src={logo} alt="logo"/>
        </div>
    </Link>
);
