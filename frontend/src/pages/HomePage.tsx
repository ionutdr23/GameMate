import { Layout, Typography } from "antd";

const { Content } = Layout;
const { Title, Paragraph } = Typography;

const HomePage = () => {
  return (
    <Layout>
      <Content style={{ padding: "50px", textAlign: "center" }}>
        <Title>Welcome to GameMate</Title>
        <Paragraph>Connect with friends and enjoy gaming together!</Paragraph>
      </Content>
    </Layout>
  );
};

export default HomePage;
