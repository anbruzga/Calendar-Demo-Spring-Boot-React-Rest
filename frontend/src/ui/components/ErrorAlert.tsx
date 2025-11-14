import React from "react";

type ErrorAlertProps = {
    message?: string | null;
};

const ErrorAlert: React.FC<ErrorAlertProps> = ({ message }) => {
    if (!message) return null;
    return <div className="error-alert">{message}</div>;
};

export default ErrorAlert;
