import React from 'react';
import ReactDOM from 'react-dom/client';
import { Greeting } from './components/Greeting/Greeting.tsx';
import { UserList } from './components/UserList/UserList.tsx';

const rootElement = document.getElementById('root');
if (!rootElement) throw new Error('Failed to find the root element');

ReactDOM.createRoot(rootElement).render(
  <React.StrictMode>
    <Greeting />
    <UserList />
  </React.StrictMode>
);