import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { ApiService } from '../../api.service';
import { mensagemErro } from '../../http-erro';
import { Pedido, PedidoFiltros } from '../../models';

@Component({
  selector: 'app-pedidos',
  imports: [FormsModule, DatePipe, CurrencyPipe],
  templateUrl: './pedidos.html',
  styleUrl: './pedidos.css'
})
export class PedidosComponent {
  private readonly api = inject(ApiService);
  private readonly rota = inject(ActivatedRoute);

  numeroPedido = '';
  cliente = '';
  dataPedido = '';
  importacaoId = '';

  readonly pedidos = signal<Pedido[]>([]);
  readonly carregando = signal(false);
  readonly erro = signal<string | null>(null);
  readonly buscou = signal(false);

  constructor() {
    const importacaoId = this.rota.snapshot.queryParamMap.get('importacaoId');
    if (importacaoId) {
      this.importacaoId = importacaoId;
    }
    this.buscar();
  }

  buscar(): void {
    const filtros: PedidoFiltros = {};
    if (this.numeroPedido.trim()) {
      filtros.numeroPedido = this.numeroPedido.trim();
    }
    if (this.cliente.trim()) {
      filtros.cliente = this.cliente.trim();
    }
    if (this.dataPedido) {
      filtros.dataPedido = this.dataPedido;
    }
    if (this.importacaoId.trim()) {
      filtros.importacaoId = Number(this.importacaoId.trim());
    }

    this.carregando.set(true);
    this.erro.set(null);

    this.api.listarPedidos(filtros).subscribe({
      next: (lista) => {
        this.pedidos.set(lista);
        this.carregando.set(false);
        this.buscou.set(true);
      },
      error: (e) => {
        this.erro.set(mensagemErro(e));
        this.carregando.set(false);
        this.buscou.set(true);
      }
    });
  }

  limpar(): void {
    this.numeroPedido = '';
    this.cliente = '';
    this.dataPedido = '';
    this.importacaoId = '';
    this.buscar();
  }
}
