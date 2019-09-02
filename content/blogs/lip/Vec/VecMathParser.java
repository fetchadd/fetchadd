// $ANTLR 3.5.2 VecMath.g 2019-09-02 11:03:22

import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class VecMathParser extends Parser {
	public static final String[] tokenNames = new String[] {
		"<invalid>", "<EOR>", "<DOWN>", "<UP>", "ID", "INT", "WS", "'*'", "'+'", 
		"','", "'.'", "'='", "'['", "']'", "'print'"
	};
	public static final int EOF=-1;
	public static final int T__7=7;
	public static final int T__8=8;
	public static final int T__9=9;
	public static final int T__10=10;
	public static final int T__11=11;
	public static final int T__12=12;
	public static final int T__13=13;
	public static final int T__14=14;
	public static final int ID=4;
	public static final int INT=5;
	public static final int WS=6;

	// delegates
	public Parser[] getDelegates() {
		return new Parser[] {};
	}

	// delegators


	public VecMathParser(TokenStream input) {
		this(input, new RecognizerSharedState());
	}
	public VecMathParser(TokenStream input, RecognizerSharedState state) {
		super(input, state);
	}

	@Override public String[] getTokenNames() { return VecMathParser.tokenNames; }
	@Override public String getGrammarFileName() { return "VecMath.g"; }



	// $ANTLR start "prog"
	// VecMath.g:4:1: prog : ( stat )+ ;
	public final void prog() throws RecognitionException {
		try {
			// VecMath.g:4:6: ( ( stat )+ )
			// VecMath.g:4:8: ( stat )+
			{
			// VecMath.g:4:8: ( stat )+
			int cnt1=0;
			loop1:
			while (true) {
				int alt1=2;
				int LA1_0 = input.LA(1);
				if ( (LA1_0==ID||LA1_0==14) ) {
					alt1=1;
				}

				switch (alt1) {
				case 1 :
					// VecMath.g:4:8: stat
					{
					pushFollow(FOLLOW_stat_in_prog11);
					stat();
					state._fsp--;

					}
					break;

				default :
					if ( cnt1 >= 1 ) break loop1;
					EarlyExitException eee = new EarlyExitException(1, input);
					throw eee;
				}
				cnt1++;
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "prog"



	// $ANTLR start "stat"
	// VecMath.g:5:1: stat : ( ID '=' expr | 'print' expr );
	public final void stat() throws RecognitionException {
		try {
			// VecMath.g:5:5: ( ID '=' expr | 'print' expr )
			int alt2=2;
			int LA2_0 = input.LA(1);
			if ( (LA2_0==ID) ) {
				alt2=1;
			}
			else if ( (LA2_0==14) ) {
				alt2=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 2, 0, input);
				throw nvae;
			}

			switch (alt2) {
				case 1 :
					// VecMath.g:5:7: ID '=' expr
					{
					match(input,ID,FOLLOW_ID_in_stat23); 
					match(input,11,FOLLOW_11_in_stat25); 
					pushFollow(FOLLOW_expr_in_stat27);
					expr();
					state._fsp--;

					}
					break;
				case 2 :
					// VecMath.g:6:7: 'print' expr
					{
					match(input,14,FOLLOW_14_in_stat40); 
					pushFollow(FOLLOW_expr_in_stat42);
					expr();
					state._fsp--;

					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "stat"



	// $ANTLR start "expr"
	// VecMath.g:11:1: expr : multExpr ( '+' multExpr )* ;
	public final void expr() throws RecognitionException {
		try {
			// VecMath.g:11:5: ( multExpr ( '+' multExpr )* )
			// VecMath.g:11:9: multExpr ( '+' multExpr )*
			{
			pushFollow(FOLLOW_multExpr_in_expr62);
			multExpr();
			state._fsp--;

			// VecMath.g:11:18: ( '+' multExpr )*
			loop3:
			while (true) {
				int alt3=2;
				int LA3_0 = input.LA(1);
				if ( (LA3_0==8) ) {
					alt3=1;
				}

				switch (alt3) {
				case 1 :
					// VecMath.g:11:19: '+' multExpr
					{
					match(input,8,FOLLOW_8_in_expr65); 
					pushFollow(FOLLOW_multExpr_in_expr67);
					multExpr();
					state._fsp--;

					}
					break;

				default :
					break loop3;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "expr"



	// $ANTLR start "multExpr"
	// VecMath.g:12:1: multExpr : primary ( ( '*' | '.' ) primary )* ;
	public final void multExpr() throws RecognitionException {
		try {
			// VecMath.g:12:9: ( primary ( ( '*' | '.' ) primary )* )
			// VecMath.g:12:11: primary ( ( '*' | '.' ) primary )*
			{
			pushFollow(FOLLOW_primary_in_multExpr83);
			primary();
			state._fsp--;

			// VecMath.g:12:19: ( ( '*' | '.' ) primary )*
			loop4:
			while (true) {
				int alt4=2;
				int LA4_0 = input.LA(1);
				if ( (LA4_0==7||LA4_0==10) ) {
					alt4=1;
				}

				switch (alt4) {
				case 1 :
					// VecMath.g:12:20: ( '*' | '.' ) primary
					{
					if ( input.LA(1)==7||input.LA(1)==10 ) {
						input.consume();
						state.errorRecovery=false;
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					pushFollow(FOLLOW_primary_in_multExpr92);
					primary();
					state._fsp--;

					}
					break;

				default :
					break loop4;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "multExpr"



	// $ANTLR start "primary"
	// VecMath.g:13:1: primary : ( INT | ID | '[' expr ( ',' expr )* ']' );
	public final void primary() throws RecognitionException {
		try {
			// VecMath.g:14:5: ( INT | ID | '[' expr ( ',' expr )* ']' )
			int alt6=3;
			switch ( input.LA(1) ) {
			case INT:
				{
				alt6=1;
				}
				break;
			case ID:
				{
				alt6=2;
				}
				break;
			case 12:
				{
				alt6=3;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 6, 0, input);
				throw nvae;
			}
			switch (alt6) {
				case 1 :
					// VecMath.g:14:9: INT
					{
					match(input,INT,FOLLOW_INT_in_primary109); 
					}
					break;
				case 2 :
					// VecMath.g:15:9: ID
					{
					match(input,ID,FOLLOW_ID_in_primary149); 
					}
					break;
				case 3 :
					// VecMath.g:16:9: '[' expr ( ',' expr )* ']'
					{
					match(input,12,FOLLOW_12_in_primary190); 
					pushFollow(FOLLOW_expr_in_primary192);
					expr();
					state._fsp--;

					// VecMath.g:16:18: ( ',' expr )*
					loop5:
					while (true) {
						int alt5=2;
						int LA5_0 = input.LA(1);
						if ( (LA5_0==9) ) {
							alt5=1;
						}

						switch (alt5) {
						case 1 :
							// VecMath.g:16:19: ',' expr
							{
							match(input,9,FOLLOW_9_in_primary195); 
							pushFollow(FOLLOW_expr_in_primary197);
							expr();
							state._fsp--;

							}
							break;

						default :
							break loop5;
						}
					}

					match(input,13,FOLLOW_13_in_primary201); 
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "primary"

	// Delegated rules



	public static final BitSet FOLLOW_stat_in_prog11 = new BitSet(new long[]{0x0000000000004012L});
	public static final BitSet FOLLOW_ID_in_stat23 = new BitSet(new long[]{0x0000000000000800L});
	public static final BitSet FOLLOW_11_in_stat25 = new BitSet(new long[]{0x0000000000001030L});
	public static final BitSet FOLLOW_expr_in_stat27 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_14_in_stat40 = new BitSet(new long[]{0x0000000000001030L});
	public static final BitSet FOLLOW_expr_in_stat42 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_multExpr_in_expr62 = new BitSet(new long[]{0x0000000000000102L});
	public static final BitSet FOLLOW_8_in_expr65 = new BitSet(new long[]{0x0000000000001030L});
	public static final BitSet FOLLOW_multExpr_in_expr67 = new BitSet(new long[]{0x0000000000000102L});
	public static final BitSet FOLLOW_primary_in_multExpr83 = new BitSet(new long[]{0x0000000000000482L});
	public static final BitSet FOLLOW_set_in_multExpr86 = new BitSet(new long[]{0x0000000000001030L});
	public static final BitSet FOLLOW_primary_in_multExpr92 = new BitSet(new long[]{0x0000000000000482L});
	public static final BitSet FOLLOW_INT_in_primary109 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_primary149 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_12_in_primary190 = new BitSet(new long[]{0x0000000000001030L});
	public static final BitSet FOLLOW_expr_in_primary192 = new BitSet(new long[]{0x0000000000002200L});
	public static final BitSet FOLLOW_9_in_primary195 = new BitSet(new long[]{0x0000000000001030L});
	public static final BitSet FOLLOW_expr_in_primary197 = new BitSet(new long[]{0x0000000000002200L});
	public static final BitSet FOLLOW_13_in_primary201 = new BitSet(new long[]{0x0000000000000002L});
}
