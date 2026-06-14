export type StatusImportacao =
  | 'RECEBIDA'
  | 'PROCESSANDO'
  | 'CONCLUIDA'
  | 'CONCLUIDA_COM_ERROS'
  | 'FALHOU';

export interface Importacao {
  id: number;
  nomeArquivo: string;
  status: StatusImportacao;
  totalLinhas: number;
  linhasValidas: number;
  linhasInvalidas: number;
  criadoEm: string;
}

export interface Pedido {
  id: number;
  numeroPedido: string;
  cliente: string;
  documentoCliente: string;
  produto: string;
  quantidade: number;
  valorUnitario: number;
  valorTotal: number;
  dataPedido: string;
  criadoEm: string;
}

export interface ErroImportacao {
  id: number;
  linha: number;
  numeroPedido: string;
  campo: string;
  mensagem: string;
  valorRecebido: string;
}

export interface DetalheImportacao {
  importacao: Importacao;
  pedidos: Pedido[];
  erros: ErroImportacao[];
}

export interface UploadResposta {
  id: number;
}

export interface PedidoFiltros {
  numeroPedido?: string;
  cliente?: string;
  dataPedido?: string;
  importacaoId?: number;
}
