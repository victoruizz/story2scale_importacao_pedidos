import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ApiService } from '../../api.service';
import { mensagemErro } from '../../http-erro';
import { DetalheImportacao } from '../../models';
import { classeStatus, rotuloStatus } from '../../status';

@Component({
  selector: 'app-detalhe',
  imports: [DatePipe, CurrencyPipe, RouterLink],
  templateUrl: './detalhe.html',
  styleUrl: './detalhe.css'
})
export class DetalheComponent {
  private readonly api = inject(ApiService);
  private readonly rota = inject(ActivatedRoute);

  readonly detalhe = signal<DetalheImportacao | null>(null);
  readonly carregando = signal(false);
  readonly erro = signal<string | null>(null);
  readonly id = signal<number | null>(null);

  readonly rotuloStatus = rotuloStatus;
  readonly classeStatus = classeStatus;

  constructor() {
    const id = Number(this.rota.snapshot.paramMap.get('id'));
    this.id.set(id);
    this.carregar(id);
  }

  carregar(id: number): void {
    this.carregando.set(true);
    this.erro.set(null);

    this.api.detalharImportacao(id).subscribe({
      next: (dados) => {
        this.detalhe.set(dados);
        this.carregando.set(false);
      },
      error: (e) => {
        this.erro.set(mensagemErro(e));
        this.carregando.set(false);
      }
    });
  }
}
