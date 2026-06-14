import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import {
  DetalheImportacao,
  Importacao,
  Pedido,
  PedidoFiltros,
  UploadResposta
} from './models';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8080';

  enviarImportacao(arquivo: File): Observable<UploadResposta> {
    const form = new FormData();
    form.append('arquivo', arquivo);
    return this.http.post<UploadResposta>(`${this.baseUrl}/importacoes`, form);
  }

  listarImportacoes(): Observable<Importacao[]> {
    return this.http.get<Importacao[]>(`${this.baseUrl}/importacoes`);
  }

  detalharImportacao(id: number): Observable<DetalheImportacao> {
    return this.http.get<DetalheImportacao>(`${this.baseUrl}/importacoes/${id}`);
  }

  listarPedidos(filtros: PedidoFiltros = {}): Observable<Pedido[]> {
    let params = new HttpParams();
    if (filtros.numeroPedido) {
      params = params.set('numeroPedido', filtros.numeroPedido);
    }
    if (filtros.cliente) {
      params = params.set('cliente', filtros.cliente);
    }
    if (filtros.dataPedido) {
      params = params.set('dataPedido', filtros.dataPedido);
    }
    if (filtros.importacaoId != null) {
      params = params.set('importacaoId', String(filtros.importacaoId));
    }
    return this.http.get<Pedido[]>(`${this.baseUrl}/pedidos`, { params });
  }
}
