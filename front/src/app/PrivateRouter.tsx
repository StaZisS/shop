import React, {ReactNode} from "react";
import {Navigate} from "react-router-dom";
import {useSelector} from "react-redux";
import {selectIsAuthenticated} from "@/modules/auth/slice";

interface PrivateRouteProps {
    children: ReactNode;
}

const PrivateRoute: React.FC<PrivateRouteProps> = ({
                                                       children,
                                                   }: PrivateRouteProps) => {
    const isAuthenticated = useSelector(selectIsAuthenticated);

    if (isAuthenticated) {
        return <>{children}</>;
    }

    return <Navigate to="/login"/>;
};

export default PrivateRoute;
