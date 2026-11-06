/* Grammar for Hibana */

parser grammar Grammar;

tokens { NEWLINE, REVR, CHOP, BLOOM, HARVEST, SEED, LIGHT, DARK, OR, STRINGLIT, VAR, ASSIGN, PLUS, LESSTHAN, NOT, B2S, LPAREN, RPAREN, SPARK, EMBER, KINDLE, SNUFF, FORGE, FLARE }

program
    : stmt_list EOF           # ProgramRoot
    ;

stmt_list
    : stmt NEWLINE stmt_list  # StmtListRec
    |                         # StmtListEmpty
    ;

stmt
    : BLOOM expr                                     # BloomStmt
    | SEED VAR ASSIGN expr                           # SeedStmt
    | SPARK expr stmt_list SNUFF                     # SparkStmt
    | SPARK expr stmt_list EMBER stmt_list SNUFF     # SparkEmberStmt
    | KINDLE expr stmt_list SNUFF                    # KindleStmt
    | FLARE expr                                     # FlareStmt
    | expr                                           # ExprStmt
    |                                                # EmptyStmt
    ;

expr
    : LPAREN expr RPAREN                             # ParenExpr
    | FORGE LPAREN id_list RPAREN stmt_list SNUFF    # ForgeExpr
    | expr LPAREN expr_list RPAREN                   # CallExpr
    | expr REVR                                      # RevrExpr
    | NOT expr                                       # NotExpr
    | B2S expr                                       # B2SExpr
    | expr CHOP expr                                 # ChopExpr
    | expr PLUS expr                                 # PlusExpr
    | expr LESSTHAN expr                             # LessThanExpr
    | expr OR expr                                   # OrExpr
    | HARVEST                                        # HarvestExpr
    | STRINGLIT                                      # StringLitExpr
    | LIGHT                                          # LightExpr
    | DARK                                           # DarkExpr
    | VAR                                            # VarExpr
    ;

id_list
    : VAR ASSIGN id_list                             # ManyIdList
    | VAR                                            # SingleIdList
    |                                                # EmptyIdList
    ;

expr_list
    : expr ASSIGN expr_list                          # ManyExprList
    | expr                                           # SingleExprList
    |                                                # EmptyExprList
    ;
