import {Link} from "react-router-dom";
import mainPageLogo from "@/assets/img/main_page_logo.svg";
import s from "./HeaderLinksIcons.module.scss";

const HeaderLinksIcons = () => {
    return (
        <div className={s.HeaderLinksIcons}>
            <Link to="/">
                <img className={s.icon} src={mainPageLogo} alt="Главная"/>
            </Link>
        </div>
    );
};

export default HeaderLinksIcons;
