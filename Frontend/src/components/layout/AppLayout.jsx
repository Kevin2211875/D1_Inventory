import Sidebar from './Sidebar';
import Topbar from './Topbar';

export default function AppLayout({ title, role, children, onLogout }) {
  return (
    <div className="app-layout">
      <Sidebar role={role} />
      <div className="app-content">
        <Topbar title={title} onLogout={onLogout} />
        <main className="page-content">{children}</main>
      </div>
    </div>
  );
}