import s from './Footer.module.scss';

const Footer = () => (
    <footer className={s.footer}>
        <div className={s.container}>
            <span className={s.name}>Дэнс дэнс</span>
            <span>2030</span>
        </div>
    </footer>
);
export default Footer;
