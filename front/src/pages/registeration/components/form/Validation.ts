import * as Yup from "yup";
export const validationSchema = Yup.object(
    {
        email: Yup.string()
            .email("Invalid email address")
            .required("Обязательное поле"),
        password: Yup.string()
            .min(6, "Пароль должен содержать не менее 6 символов")
            .required("Обязательное поле"),
        fullName: Yup.string().required("Обязательное поле"),
    }
);