import { useEffect, useState, useCallback } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

const WEBSOCKET_URL = import.meta.env.VITE_WS_URL || 'http://localhost:8080/ws';

export const useWebSocket = (topics = []) => {
  const [messages, setMessages] = useState({});
  const [connected, setConnected] = useState(false);
  const [stompClient, setStompClient] = useState(null);

  useEffect(() => {
    const client = new Client({
      webSocketFactory: () => new SockJS(WEBSOCKET_URL),
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      onConnect: () => {
        setConnected(true);
        topics.forEach(topic => {
          client.subscribe(topic, (message) => {
            if (message.body) {
              const parsedMessage = JSON.parse(message.body);
              setMessages(prev => ({
                ...prev,
                [topic]: [...(prev[topic] || []), parsedMessage]
              }));
            }
          });
        });
      },
      onDisconnect: () => {
        setConnected(false);
      },
      onStompError: (frame) => {
        console.error('Broker reported error: ' + frame.headers['message']);
        console.error('Additional details: ' + frame.body);
      },
    });

    client.activate();
    setStompClient(client);

    return () => {
      client.deactivate();
    };
  }, [JSON.stringify(topics)]); // Re-run if topics array changes

  const sendMessage = useCallback((destination, body) => {
    if (stompClient && connected) {
      stompClient.publish({ destination, body: JSON.stringify(body) });
    }
  }, [stompClient, connected]);

  return { messages, connected, sendMessage };
};
