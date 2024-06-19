import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import java.util.Hashtable;
import java.util.Scanner;

public class ClassicLDAPinjection {

    public void BadLDAPlogin() throws NamingException {

        Scanner console = new Scanner(System.in);
        System.out.println("Enter username");
        String username = console.nextLine();

        if (username == null || username.isEmpty()) {
            throw new RuntimeException("No access without username for you!");
        }

        System.out.println("Enter password");
        String password = console.nextLine();

        if (password == null || password.isEmpty()) {
            throw new RuntimeException("No access without password for you!");
        }


        String query = String.format("(&(uid=%s)(userPassword=%s))", username, password);
        System.out.println("LDAP query: " + query);

        Hashtable<String, Object> env = new Hashtable<>();
        env.put("java.naming.provider.url", "ldap://localhost:8080/dc=example,dc=org");
        env.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
        InitialLdapContext ctx = new InitialLdapContext(env, null);

        SearchControls constraints = new SearchControls();
        constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
        constraints.setReturningAttributes(new String[0]);      //return no attrs

        NamingEnumeration<SearchResult> results = ctx.search("", query, constraints);
        try {
            if (results.hasMore()) {
                System.out.println("Access granted");
            } else {
                System.out.println("Access denied");
            }
        } finally {
            results.close();
        }
    }
}

