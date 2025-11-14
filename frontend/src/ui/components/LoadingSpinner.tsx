import React from "react";

const LoadingSpinner: React.FC = () => {
    return (
        <div className="loading-spinner" aria-label="Loading">
            <div className="loading-spinner__circle" />
        </div>
    );
};

export default LoadingSpinner;
