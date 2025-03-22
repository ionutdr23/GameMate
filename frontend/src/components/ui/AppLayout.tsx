import { Layout } from "antd";
import Navbar from "./Navbar";
import { Outlet } from "react-router-dom";

const { Header, Content, Footer } = Layout;

const AppLayout = () => {
  return (
    <Layout style={{ minHeight: "100vh" }}>
      <Header style={{ padding: 0 }}>
        <Navbar />
      </Header>
      <Content style={{ padding: "20px 50px" }}>
        <Outlet />
      </Content>
      <Footer style={{ textAlign: "center" }}>
        GameMate ©{new Date().getFullYear()}
      </Footer>
    </Layout>
  );
};

export default AppLayout;
