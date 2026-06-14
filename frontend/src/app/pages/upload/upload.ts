import { Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../api.service';
import { mensagemErro } from '../../http-erro';

@Component({
  selector: 'app-upload',
  imports: [RouterLink],
  templateUrl: './upload.html',
  styleUrl: './upload.css'
})
export class UploadComponent {
  private readonly api = inject(ApiService);

  readonly arquivo = signal<File | null>(null);
  readonly enviando = signal(false);
  readonly erro = signal<string | null>(null);
  readonly idCriado = signal<number | null>(null);

  selecionar(evento: Event): void {
    const input = evento.target as HTMLInputElement;
    const arquivo = input.files?.[0] ?? null;
    this.erro.set(null);
    this.idCriado.set(null);

    if (arquivo && !arquivo.name.toLowerCase().endsWith('.csv')) {
      this.arquivo.set(null);
      this.erro.set('Selecione um arquivo .csv valido.');
      input.value = '';
      return;
    }

    this.arquivo.set(arquivo);
  }

  enviar(): void {
    const arquivo = this.arquivo();
    if (!arquivo || this.enviando()) {
      return;
    }

    this.enviando.set(true);
    this.erro.set(null);
    this.idCriado.set(null);

    this.api.enviarImportacao(arquivo).subscribe({
      next: (resposta) => {
        this.enviando.set(false);
        this.idCriado.set(resposta.id);
        this.arquivo.set(null);
      },
      error: (e) => {
        this.enviando.set(false);
        this.erro.set(mensagemErro(e));
      }
    });
  }
}
