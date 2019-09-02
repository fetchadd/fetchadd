// $ANTLR 3.5.2 VecMath.g 2019-09-02 11:03:22

import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class VecMathLexer extends Lexer {
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
	// delegators
	public Lexer[] getDelegates() {
		return new Lexer[] {};
	}

	public VecMathLexer() {} 
	public VecMathLexer(CharStream input) {
		this(input, new RecognizerSharedState());
	}
	public VecMathLexer(CharStream input, RecognizerSharedState state) {
		super(input,state);
	}
	@Override public String getGrammarFileName() { return "VecMath.g"; }

	// $ANTLR start "T__7"
	public final void mT__7() throws RecognitionException {
		try {
			int _type = T__7;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// VecMath.g:2:6: ( '*' )
			// VecMath.g:2:8: '*'
			{
			match('*'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__7"

	// $ANTLR start "T__8"
	public final void mT__8() throws RecognitionException {
		try {
			int _type = T__8;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// VecMath.g:3:6: ( '+' )
			// VecMath.g:3:8: '+'
			{
			match('+'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__8"

	// $ANTLR start "T__9"
	public final void mT__9() throws RecognitionException {
		try {
			int _type = T__9;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// VecMath.g:4:6: ( ',' )
			// VecMath.g:4:8: ','
			{
			match(','); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__9"

	// $ANTLR start "T__10"
	public final void mT__10() throws RecognitionException {
		try {
			int _type = T__10;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// VecMath.g:5:7: ( '.' )
			// VecMath.g:5:9: '.'
			{
			match('.'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__10"

	// $ANTLR start "T__11"
	public final void mT__11() throws RecognitionException {
		try {
			int _type = T__11;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// VecMath.g:6:7: ( '=' )
			// VecMath.g:6:9: '='
			{
			match('='); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__11"

	// $ANTLR start "T__12"
	public final void mT__12() throws RecognitionException {
		try {
			int _type = T__12;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// VecMath.g:7:7: ( '[' )
			// VecMath.g:7:9: '['
			{
			match('['); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__12"

	// $ANTLR start "T__13"
	public final void mT__13() throws RecognitionException {
		try {
			int _type = T__13;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// VecMath.g:8:7: ( ']' )
			// VecMath.g:8:9: ']'
			{
			match(']'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__13"

	// $ANTLR start "T__14"
	public final void mT__14() throws RecognitionException {
		try {
			int _type = T__14;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// VecMath.g:9:7: ( 'print' )
			// VecMath.g:9:9: 'print'
			{
			match("print"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__14"

	// $ANTLR start "ID"
	public final void mID() throws RecognitionException {
		try {
			int _type = ID;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// VecMath.g:20:5: ( ( 'a' .. 'z' )+ )
			// VecMath.g:20:9: ( 'a' .. 'z' )+
			{
			// VecMath.g:20:9: ( 'a' .. 'z' )+
			int cnt1=0;
			loop1:
			while (true) {
				int alt1=2;
				int LA1_0 = input.LA(1);
				if ( ((LA1_0 >= 'a' && LA1_0 <= 'z')) ) {
					alt1=1;
				}

				switch (alt1) {
				case 1 :
					// VecMath.g:
					{
					if ( (input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
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

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "ID"

	// $ANTLR start "INT"
	public final void mINT() throws RecognitionException {
		try {
			int _type = INT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// VecMath.g:21:5: ( ( '0' .. '9' )+ )
			// VecMath.g:21:9: ( '0' .. '9' )+
			{
			// VecMath.g:21:9: ( '0' .. '9' )+
			int cnt2=0;
			loop2:
			while (true) {
				int alt2=2;
				int LA2_0 = input.LA(1);
				if ( ((LA2_0 >= '0' && LA2_0 <= '9')) ) {
					alt2=1;
				}

				switch (alt2) {
				case 1 :
					// VecMath.g:
					{
					if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					if ( cnt2 >= 1 ) break loop2;
					EarlyExitException eee = new EarlyExitException(2, input);
					throw eee;
				}
				cnt2++;
			}

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "INT"

	// $ANTLR start "WS"
	public final void mWS() throws RecognitionException {
		try {
			int _type = WS;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// VecMath.g:22:5: ( ( ' ' | '\\r' | '\\n' )+ )
			// VecMath.g:22:9: ( ' ' | '\\r' | '\\n' )+
			{
			// VecMath.g:22:9: ( ' ' | '\\r' | '\\n' )+
			int cnt3=0;
			loop3:
			while (true) {
				int alt3=2;
				int LA3_0 = input.LA(1);
				if ( (LA3_0=='\n'||LA3_0=='\r'||LA3_0==' ') ) {
					alt3=1;
				}

				switch (alt3) {
				case 1 :
					// VecMath.g:
					{
					if ( input.LA(1)=='\n'||input.LA(1)=='\r'||input.LA(1)==' ' ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					if ( cnt3 >= 1 ) break loop3;
					EarlyExitException eee = new EarlyExitException(3, input);
					throw eee;
				}
				cnt3++;
			}

			skip();
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "WS"

	@Override
	public void mTokens() throws RecognitionException {
		// VecMath.g:1:8: ( T__7 | T__8 | T__9 | T__10 | T__11 | T__12 | T__13 | T__14 | ID | INT | WS )
		int alt4=11;
		switch ( input.LA(1) ) {
		case '*':
			{
			alt4=1;
			}
			break;
		case '+':
			{
			alt4=2;
			}
			break;
		case ',':
			{
			alt4=3;
			}
			break;
		case '.':
			{
			alt4=4;
			}
			break;
		case '=':
			{
			alt4=5;
			}
			break;
		case '[':
			{
			alt4=6;
			}
			break;
		case ']':
			{
			alt4=7;
			}
			break;
		case 'p':
			{
			int LA4_8 = input.LA(2);
			if ( (LA4_8=='r') ) {
				int LA4_12 = input.LA(3);
				if ( (LA4_12=='i') ) {
					int LA4_13 = input.LA(4);
					if ( (LA4_13=='n') ) {
						int LA4_14 = input.LA(5);
						if ( (LA4_14=='t') ) {
							int LA4_15 = input.LA(6);
							if ( ((LA4_15 >= 'a' && LA4_15 <= 'z')) ) {
								alt4=9;
							}

							else {
								alt4=8;
							}

						}

						else {
							alt4=9;
						}

					}

					else {
						alt4=9;
					}

				}

				else {
					alt4=9;
				}

			}

			else {
				alt4=9;
			}

			}
			break;
		case 'a':
		case 'b':
		case 'c':
		case 'd':
		case 'e':
		case 'f':
		case 'g':
		case 'h':
		case 'i':
		case 'j':
		case 'k':
		case 'l':
		case 'm':
		case 'n':
		case 'o':
		case 'q':
		case 'r':
		case 's':
		case 't':
		case 'u':
		case 'v':
		case 'w':
		case 'x':
		case 'y':
		case 'z':
			{
			alt4=9;
			}
			break;
		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
			{
			alt4=10;
			}
			break;
		case '\n':
		case '\r':
		case ' ':
			{
			alt4=11;
			}
			break;
		default:
			NoViableAltException nvae =
				new NoViableAltException("", 4, 0, input);
			throw nvae;
		}
		switch (alt4) {
			case 1 :
				// VecMath.g:1:10: T__7
				{
				mT__7(); 

				}
				break;
			case 2 :
				// VecMath.g:1:15: T__8
				{
				mT__8(); 

				}
				break;
			case 3 :
				// VecMath.g:1:20: T__9
				{
				mT__9(); 

				}
				break;
			case 4 :
				// VecMath.g:1:25: T__10
				{
				mT__10(); 

				}
				break;
			case 5 :
				// VecMath.g:1:31: T__11
				{
				mT__11(); 

				}
				break;
			case 6 :
				// VecMath.g:1:37: T__12
				{
				mT__12(); 

				}
				break;
			case 7 :
				// VecMath.g:1:43: T__13
				{
				mT__13(); 

				}
				break;
			case 8 :
				// VecMath.g:1:49: T__14
				{
				mT__14(); 

				}
				break;
			case 9 :
				// VecMath.g:1:55: ID
				{
				mID(); 

				}
				break;
			case 10 :
				// VecMath.g:1:58: INT
				{
				mINT(); 

				}
				break;
			case 11 :
				// VecMath.g:1:62: WS
				{
				mWS(); 

				}
				break;

		}
	}



}
