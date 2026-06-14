import { DatePipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { ApiService } from '../../api.service';
import { mensagemErro } from '../../http-erro';
import { Importacao } from '../../models';
import { classeStatus, rotuloStatus } from '../../status';

@Component({
  selector: 'app-historico',
  imports: [DatePipe],
  templateUrl: './historico.html',
  styleUrl: './historico.css'
})
export class HistoricoComponent {
  private readonly api = inject(ApiService);
  private readonly router = inject(Router);

  readonly importacoes = signal<Importacao[]>([]);
  readonly carregando = signal(false);
  readonly erro = signal<string | null>(null);

  readonly rotuloStatus = rotuloStatus;
  readonly classeStatus = classeStatus;

  constructor() {
    this.carregar();
  }

  carregar(): void {
    this.carregando.set(true);
    this.erro.set(null);

    this.api.listarImportacoes().subscribe({
      next: (lista) => {
        this.importacoes.set(lista);
        this.carregando.set(false);
      },
      error: (e) => {
        this.erro.set(mensagemErro(e));
        this.carregando.set(false);
      }
    });
  }

  abrir(importacao: Importacao): void {
    this.router.navigate(['/importacoes', importacao.id]);
  }
}
