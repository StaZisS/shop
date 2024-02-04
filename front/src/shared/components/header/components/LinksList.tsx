import {Link} from "react-router-dom";
import s from "./LinksList.module.scss";

export const LinksList = () => {
    return (
        <div className={s.LinksList}>

            <Link to={"/"} className={s.item}>
                Главная
            </Link>


        </div>
    );
};
