import {HeaderLogo} from "../components/HeaderLogo";
import {LinksList} from "../components/LinksList";
import s from "./Header.module.scss";
import HeaderLinksIcons from "@/shared/components/header/components/HeaderLinksIcons.tsx";


export const Header = () => {
    return (
        <div className={s.header}>
            <HeaderLogo/>
            <LinksList/>
            <HeaderLinksIcons/>
        </div>
    );
};

export default Header;
