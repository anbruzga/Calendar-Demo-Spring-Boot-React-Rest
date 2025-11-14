import React from "react";

type ConfirmDialogProps = {
    open: boolean;
    title?: string;
    message: string;
    confirmLabel?: string;
    cancelLabel?: string;
    onConfirm: () => void;
    onCancel: () => void;
};

const ConfirmDialog: React.FC<ConfirmDialogProps> = ({
                                                         open,
                                                         title = "Are you sure?",
                                                         message,
                                                         confirmLabel = "Yes",
                                                         cancelLabel = "Cancel",
                                                         onConfirm,
                                                         onCancel,
                                                     }) => {
    if (!open) return null;

    return (
        <div className="confirm-dialog__backdrop">
            <div className="confirm-dialog">
                {title && <h2 className="confirm-dialog__title">{title}</h2>}
                <p className="confirm-dialog__message">{message}</p>
                <div className="confirm-dialog__actions">
                    <button
                        type="button"
                        className="confirm-dialog__button confirm-dialog__button--secondary"
                        onClick={onCancel}
                    >
                        {cancelLabel}
                    </button>
                    <button
                        type="button"
                        className="confirm-dialog__button confirm-dialog__button--primary"
                        onClick={onConfirm}
                    >
                        {confirmLabel}
                    </button>
                </div>
            </div>
        </div>
    );
};

export default ConfirmDialog;
