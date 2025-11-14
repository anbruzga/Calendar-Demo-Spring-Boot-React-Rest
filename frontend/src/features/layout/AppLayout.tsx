import type { ReactNode, FC } from "react";

type AppLayoutProps = {
    title?: string;
    children: ReactNode;
};

const AppLayout: FC<AppLayoutProps> = ({
                                           title = "Calendar reminders",
                                           children,
                                       }) => {
    return (
        <div className="app-layout">
            <header className="app-header">
                <h1 className="app-title">{title}</h1>
            </header>
            <main className="app-main">{children}</main>
        </div>
    );
};

export default AppLayout;
