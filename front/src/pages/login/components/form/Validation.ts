import * as Yup from "yup";
export const validationSchema = Yup.object(
    {
        email: Yup.string()
            .email("Invalid email address")
            .required("Обязательное поле"),
        password: Yup.string().required("Обязательное поле"),
    }
);