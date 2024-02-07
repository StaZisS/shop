import {createSlice, PayloadAction} from "@reduxjs/toolkit";
import {RootState} from "@/store/store.tsx";
import {ACCESS_TOKEN_NAME} from "@/shared/constants/jwt.ts";

interface AuthState {
    token: string | null;
    isAuthenticated: boolean;
}

const initialState: AuthState = {
    token: localStorage.getItem(ACCESS_TOKEN_NAME),
    isAuthenticated: Boolean(localStorage.getItem(ACCESS_TOKEN_NAME)),
};

const authSlice = createSlice({
    name: "auth",
    initialState,
    reducers: {
        setToken: (state, action: PayloadAction<string>) => {
            state.token = action.payload;
            state.isAuthenticated = true;
            localStorage.setItem(ACCESS_TOKEN_NAME, action.payload);
        },
        clearToken: (state) => {
            state.token = null;
            state.isAuthenticated = false;
            localStorage.removeItem(ACCESS_TOKEN_NAME);
        },
    },
});

export const selectIsAuthenticated = (state: RootState) =>
    state.auth.isAuthenticated;
export const {setToken, clearToken} = authSlice.actions;
export default authSlice.reducer;
