import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import java.util.Hashtable;
import java.util.Scanner;

public class BlindLDAPinjection {
    public void  BlindLDAPinject() throws NamingException {

        Scanner console = new Scanner(System.in);
        System.out.println("Enter uid");
        String uid = console.nextLine();

        if (uid == null || uid.isEmpty()) {
            throw new RuntimeException("I need uid!");
        }
        String query = String.format("(&(uid=%s)(objectClass=person))", uid);
        System.out.println("LDAP query: " + query);

        Hashtable<String, Object> env = new Hashtable<>();
        env.put("java.naming.provider.url", "ldap://localhost:8080/dc=example,dc=org");
        env.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
        InitialLdapContext ctx = new InitialLdapContext(env, null);

        SearchControls constraints = new SearchControls();
        constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
        constraints.setReturningAttributes(new String[] { "telephoneNumber" });

        NamingEnumeration<SearchResult> results = ctx.search("", query, constraints);
        try {
            if (!results.hasMore()) {
                System.out.println("Nobody found!");
            } else {
                Object phone = results.next().getAttributes().get("telephoneNumber");
                System.out.println("Phone: " + phone);
            }
        } finally {
            results.close();
        }
    }
}
