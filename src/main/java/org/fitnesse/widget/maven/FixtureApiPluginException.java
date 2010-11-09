package org.fitnesse.widget.maven;

/**
 * Fixture api plugin exception.
 * @author asikkema
 */
public class FixtureApiPluginException extends RuntimeException {
    private static final long serialVersionUID = -4333629710110950818L;

    /** Constructor. */
    public FixtureApiPluginException() {
    }

    /**
     * Constructor.
     * @param message The error message.
     */
    public FixtureApiPluginException(final String message) {
        super(message);
    }

    /**
     * Constructor.
     * @param cause The throwable cause.
     */
    public FixtureApiPluginException(final Throwable cause) {
        super(cause);
    }

    /**
     * Constructor.
     * @param message The error message.
     * @param cause The throwable cause.
     */
    public FixtureApiPluginException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
