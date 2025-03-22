import { useState } from "react";
import { Input, Button, List, Avatar, Typography } from "antd";

const { Title } = Typography;

const FriendsPage = () => {
  const [searchQuery, setSearchQuery] = useState("");
  const [friends] = useState([
    { id: 1, name: "Jane Smith", avatar: "https://bit.ly/prosper-baba" },
    { id: 2, name: "Mike Johnson", avatar: "https://bit.ly/ryan-florence" },
  ]);
  const [friendRequests] = useState([
    { id: 3, name: "Alice Cooper", avatar: "https://bit.ly/code-beast" },
  ]);

  const handleSearch = () => {
    console.log("Searching for:", searchQuery);
  };

  return (
    <div style={{ padding: "50px" }}>
      <Title level={2}>Find Friends</Title>

      {/* Search Bar */}
      <Input.Search
        placeholder="Search for friends..."
        value={searchQuery}
        onChange={(e) => setSearchQuery(e.target.value)}
        onSearch={handleSearch}
      />

      {/* Current Friends */}
      <Title level={3}>Your Friends</Title>
      <List
        itemLayout="horizontal"
        dataSource={friends}
        renderItem={(friend) => (
          <List.Item key={friend.id}>
            <List.Item.Meta
              avatar={<Avatar src={friend.avatar} />}
              title={friend.name}
            />
            <Button color="danger">Remove</Button>
          </List.Item>
        )}
      />

      {/* Friend Requests */}
      <Title level={3}>Friend Requests</Title>
      <List
        itemLayout="horizontal"
        dataSource={friendRequests}
        renderItem={(request) => (
          <List.Item key={request.id}>
            <List.Item.Meta
              avatar={<Avatar src={request.avatar} />}
              title={request.name}
            />
            <Button type="primary">Accept</Button>
            <Button>Decline</Button>
          </List.Item>
        )}
      />
    </div>
  );
};

export default FriendsPage;
