import { Avatar, Typography, Layout } from "antd";

const { Content } = Layout;
const { Title, Paragraph } = Typography;

interface User {
  avatar: string;
  name: string;
  email: string;
}

const ProfilePage = ({ user }: { user: User }) => {
  return (
    <Layout>
      <Content style={{ padding: "50px", textAlign: "center" }}>
        <Avatar size={100} src={user.avatar} />
        <Title level={3}>{user.name}</Title>
        <Paragraph>{user.email}</Paragraph>
      </Content>
    </Layout>
  );
};

export default ProfilePage;
