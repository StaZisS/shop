import {createAsyncThunk} from "@reduxjs/toolkit";
import {AuthData} from "@/modules/auth/types.ts";
import axios from "axios";
import {clearToken, setToken} from "@/modules/auth/slice.ts";
import {BASE_URL} from "@/shared/constants/url.ts";

export const loginUser = createAsyncThunk(
    "auth/login",
    async (data: AuthData, {dispatch}) => {
        try {
            const response = await axios.post(
                `${BASE_URL}auth/login`,
                data
            );
            const token = response.data.token;
            localStorage.setItem("token", token);
            localStorage.setItem("email", data.email);
            dispatch(setToken(token));
        } catch (error) {
            throw new Error("Login failed");
        }
    }
);

export const logoutUser = createAsyncThunk(
    "auth/logout",
    async (_, {dispatch}) => {
        try {
            const response = await axios.post(
                `${BASE_URL}auth/logout`,
                {},
                {
                    headers: {
                        Authorization: `Bearer ${localStorage.getItem("token")}`,
                    },
                }
            );

            localStorage.getItem("token");
            dispatch(clearToken());
            localStorage.clear();
        } catch (error) {
            console.error("Logout failed:", error);
            if (
                (error.response && error.response.status === 403) ||
                (error.response && error.response.status === 401)
            ) {
                dispatch(clearToken());
            }
        }
    }
);