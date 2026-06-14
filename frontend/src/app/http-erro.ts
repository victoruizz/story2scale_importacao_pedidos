import { HttpErrorResponse } from '@angular/common/http';

export function mensagemErro(erro: unknown): string {
  if (erro instanceof HttpErrorResponse) {
    if (erro.status === 0) {
      return 'Nao foi possivel conectar ao servidor. Verifique se o backend esta no ar.';
    }
    const corpo = erro.error;
    if (typeof corpo === 'string' && corpo.trim()) {
      return corpo;
    }
    if (corpo && typeof corpo === 'object' && typeof corpo.message === 'string') {
      return corpo.message;
    }
    return `Erro ${erro.status}: ${erro.statusText || 'falha na requisicao'}.`;
  }
  return 'Ocorreu um erro inesperado.';
}
