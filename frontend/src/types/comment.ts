export type Comment = {
  id: string;
  content: string;
  replies: Comment[];
};
