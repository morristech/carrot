package au.com.codeka.carrot;

import au.com.codeka.carrot.lib.Scope;
import au.com.codeka.carrot.tmpl.parse.Tokenizer;
import au.com.codeka.carrot.resource.ResourceLocater;
import au.com.codeka.carrot.resource.ResourceName;
import au.com.codeka.carrot.tmpl.Node;
import au.com.codeka.carrot.tmpl.TemplateParser;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link CarrotEngine} is the root of the carrot system. You create an instance of this, make it global or static,
 * load templates and process them from here.
 */
public class CarrotEngine {
  private final Configuration config;
  private final Map<String, Object> globalBindings;
  private final ParseCache parseCache;
  private final TemplateParser templateParser;

  /**
   * Constructs a new {@link CarrotEngine} with a default {@link Configuration}.
   *
   * <p>The configuration is mutable, so you can modify once this class has been created.
   */
  public CarrotEngine() {
    this(new Configuration());
  }

  /**
   * Constructs a new {@link CarrotEngine} with the given {@link Configuration}.
   *
   * <p>The configuration is mutable, so you can modify once this class has been created.
   *
   *  @param config The {@link Configuration} to construct this engine with.
   */
  public CarrotEngine(Configuration config) {
    this.config = config;
    this.globalBindings = new HashMap<>();
    this.parseCache = new ParseCache(config);
    this.templateParser = new TemplateParser(config);
  }

  /**
   * Gets the {@link Configuration}. The configuration is mutable, so you are able to modify settings on the value
   * returned here and they will take effect on the current {@link CarrotEngine}.
   */
  public Configuration getConfig() {
    return config;
  }

  /**
   * Get a map of the global variables. These bindings will be accessible in all templates processed by this
   * {@link CarrotEngine}.
   */
  public Map<String, Object> getGlobalBindings() {
    return globalBindings;
  }

  /**
   * Process the template with the given filename, writing the results to the given {@link Writer}.
   *
   * @param writer A {@link Writer} to write the results of processing the given template to.
   * @param templateFile The name of the template file, which will be resolved by our configured
   *                     {@link ResourceLocater}.
   * @param bindings A mapping of string to variables that make up the bindings for this template.
   *
   * @throws CarrotException Thrown if any errors occur.
   */
  public void process(
      Writer writer,
      String templateFile,
      @Nullable Map<String, Object> bindings) throws CarrotException {
    ResourceName resourceName = config.getResourceLocater().findResource(templateFile);
    Node node = parseCache.getNode(resourceName);
    if (node == null) {
      node = templateParser.parse(new Tokenizer(config.getResourceLocater().getReader(resourceName)));
      parseCache.addNode(resourceName, node);
    }

    Scope scope = new Scope(globalBindings);
    if (bindings != null) {
      scope.push(bindings);
    }
    try {
      node.render(config, writer, scope);
    } catch (IOException e) {
      throw new CarrotException(e);
    }
  }

  /**
   * Process the template with the given filename, and returns the result as a string.
   *
   * @param templateFile The name of the template file, which will be resolved by our configured
   *                     {@link ResourceLocater}.
   * @param bindings A mapping of string to variables that make up the bindings for this template.
   *
   * @throws CarrotException Thrown if any errors occur.
   */
  public String process(String templateFile, @Nullable Map<String, Object> bindings) throws CarrotException {
    StringWriter writer = new StringWriter();
    process(writer, templateFile, bindings);
    return writer.getBuffer().toString();
  }
}