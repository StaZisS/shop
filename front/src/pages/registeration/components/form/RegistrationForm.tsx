import {useNavigate} from "react-router-dom";
import {useState} from "react";
import s from "./RegistrationForm.module.scss";
import {useFormik} from "formik";
import DatePicker from "react-datepicker";
import {RegistrationData} from "@/modules/registration/types.ts";
import {AppDispatch, RootState} from "@/store/store.tsx";
import {useDispatch, useSelector} from "react-redux";
import {registerUser} from "@/modules/registration/thunk.ts";
import {selectIsAuthenticated} from "@/modules/auth/slice.ts";
import {initialValue} from "@/pages/registeration/components/form/InitialValues.ts";
import {validationSchema} from "@/pages/registeration/components/form/Validation.ts";

const RegistrationForm = () => {
    const navigate = useNavigate();
    const dispatch: AppDispatch = useDispatch();
    const [selectedDate, setSelectedDate] = useState<Date | null>(null);
    const isAuthenticated = useSelector((state: RootState) =>
        selectIsAuthenticated(state)
    );

    const formik = useFormik({
        initialValues: initialValue,
        validationSchema: validationSchema,
        onSubmit: async (values) => {
            try {
                const formattedData : RegistrationData = {
                    name: values.fullName,
                    password: values.password,
                    email: values.email,
                    birth_date: selectedDate?.toISOString(),
                    gender: values.gender,
                };
                await dispatch(registerUser(formattedData));
            } catch (error) {
                console.error("Registration failed:", error);
                throw new Error("Registration failed");
            }
        },
    });

    if (isAuthenticated) {
        navigate("/profile");
    }

    return (
        <form onSubmit={formik.handleSubmit} className={s.form}>
            <h2>Регистрация нового пользователя</h2>
            <div className={s.formWrapper}>
                <div className={s.formItem}>
                    <label htmlFor="fullName">Фио</label>
                    <input
                        id="fullName"
                        name="fullName"
                        type="text"
                        onChange={formik.handleChange}
                        onBlur={formik.handleBlur}
                        value={formik.values.fullName}
                    />
                    {formik.touched.fullName && formik.errors.fullName ? (
                        <div>{formik.errors.fullName}</div>
                    ) : null}
                </div>
                <div className={s.formItem}>
                    <label htmlFor="gender">Пол</label>
                    <select
                        id="gender"
                        name="gender"
                        onChange={formik.handleChange}
                        onBlur={formik.handleBlur}
                        value={formik.values.gender}
                    >
                        <option value="MALE">Мужчина</option>
                        <option value="FEMALE">Женщина</option>
                    </select>
                    {formik.touched.gender && formik.errors.gender ? (
                        <div>{formik.errors.gender}</div>
                    ) : null}
                </div>
                <div className={s.formItem}>
                    <label htmlFor="dob">Дата рождения:</label>
                    <DatePicker
                        id="dob"
                        name="dob"
                        selected={selectedDate}
                        onChange={(date) => {
                            setSelectedDate(date);
                            formik.setFieldValue("dob", date);
                        }}
                        dateFormat="dd/MM/yyyy"
                    />
                </div>
                <div className={s.formItem}>
                    <label htmlFor="email">Email:</label>
                    <input
                        id="email"
                        name="email"
                        type="email"
                        onChange={formik.handleChange}
                        onBlur={formik.handleBlur}
                        value={formik.values.email}
                    />
                    {formik.touched.email && formik.errors.email ? (
                        <div>{formik.errors.email}</div>
                    ) : null}
                </div>
                <div className={s.formItem}>
                    <label htmlFor="password">Password:</label>
                    <input
                        id="password"
                        name="password"
                        type="password"
                        onChange={formik.handleChange}
                        onBlur={formik.handleBlur}
                        value={formik.values.password}
                    />
                    {formik.touched.password && formik.errors.password ? (
                        <div>{formik.errors.password}</div>
                    ) : null}
                </div>
                <button className={s.registerButton} type="submit">Register</button>
            </div>
        </form>
    );
};

export default RegistrationForm;