import { Injectable } from '@angular/core';
import { Subject, Observable } from 'rxjs';

export interface WebSocketMessage {
  action: 'create' | 'update' | 'delete';
  payload?: any;
  id?: number;
}

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private socket?: WebSocket;
  private messagesSubject = new Subject<WebSocketMessage>();
  private currentEndpoint: string | null = null;

  public messages$: Observable<WebSocketMessage> = this.messagesSubject.asObservable();

  constructor() { }

  public connect(endpoint: string): void {
    if (this.currentEndpoint === endpoint && this.socket?.readyState === WebSocket.OPEN) {
      console.log(`WebSocket: Уже подключены к ${endpoint}.`);
      return;
    }

    if (this.socket) {
      this.close();
    }

    this.currentEndpoint = endpoint;
    const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
    const backendHost = 'localhost:8080';
    const url = `${protocol}//${backendHost}${endpoint}`;
    
    this.socket = new WebSocket(url);

    this.socket.onopen = (event) => {
      // console.log(`WebSocket: Соединение с ${url} установлено.`, event);
    };

    this.socket.onmessage = (event) => {
      try {
        const message = JSON.parse(event.data) as WebSocketMessage;
        this.messagesSubject.next(message);
      } catch (e) {
        console.error('WebSocket: Ошибка парсинга сообщения:', e);
      }
    };

    this.socket.onclose = (event) => {
      // console.log(`WebSocket: Соединение с ${url} закрыто.`, event);
      if (this.currentEndpoint === endpoint) {
        this.currentEndpoint = null;
      }
    };

    this.socket.onerror = (event) => {
      console.error(`WebSocket: Ошибка соединения с ${url}:`, event);
    };
  }

  public close(): void {
    if (this.socket) {
      this.socket.close();
      this.socket = undefined;
      this.currentEndpoint = null;
    }
  }
}