package com.link_intersystems.tools.git.test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import com.link_intersystems.gitdirstat.domain.GitRepository;

public class GitRepositoryTestRunner extends BlockJUnit4ClassRunner {

	TestEnvironmentProperties testEnvironmentProperties;
	GitRepositoryResolver gitRepositoryResolver;

	public GitRepositoryTestRunner(Class<?> klass) throws InitializationError,
			IOException {
		super(klass);
		testEnvironmentProperties = new TestEnvironmentProperties();
		gitRepositoryResolver = new GitRepositoryResolver(this,
				testEnvironmentProperties);
	}

	@Override
	protected void collectInitializationErrors(List<Throwable> errors) {
		validateNoNonStaticInnerClass(errors);
		validateConstructor(errors);
		validateInstanceMethods(errors);
		validateFields(errors);
		validateMethods(errors);
	}

	@Override
	protected Statement methodInvoker(FrameworkMethod method, Object test) {
		return new InvokeMethod(method, test, gitRepositoryResolver);
	}

	private void validateMethods(List<Throwable> errors) {

	}

	@Override
	protected void validateInstanceMethods(List<Throwable> errors) {
	}

	class InvokeMethod extends Statement {
		private final FrameworkMethod fTestMethod;
		private Object fTarget;
		private GitRepositoryResolver gitRepositoryResolver;

		public InvokeMethod(FrameworkMethod testMethod, Object target,
				GitRepositoryResolver gitRepositoryResolver) {
			fTestMethod = testMethod;
			fTarget = target;
			this.gitRepositoryResolver = gitRepositoryResolver;
		}

		@Override
		public void evaluate() throws Throwable {
			Object[] params = resolveParams(fTestMethod);
			fTestMethod.invokeExplosively(fTarget, params);
			for (Object object : params) {
				if (object instanceof Git) {
					Git.class.cast(object).close();
				}
			}
		}

		private Object[] resolveParams(FrameworkMethod frameworkMethod) {
			Method method = frameworkMethod.getMethod();
			Class<?>[] parameterTypes = method.getParameterTypes();
			Object[] arguments = new Object[parameterTypes.length];
			for (int i = 0; i < parameterTypes.length; i++) {
				arguments[i] = resolveArgument(frameworkMethod,
						parameterTypes[i]);
			}
			return arguments;
		}

		private Object resolveArgument(FrameworkMethod frameworkMethod,
				Class<?> parameterType) {
			Object argument = null;
			if (Git.class.equals(parameterType)) {
				argument = gitRepositoryResolver.getRepository(frameworkMethod);
			} else if (GitRepository.class.equals(parameterType)) {
				Git git = gitRepositoryResolver.getRepository(frameworkMethod);
				argument = new GitRepository(git);
			}
			return argument;
		}
	}
}
